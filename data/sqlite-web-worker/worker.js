// OPTIONAL (Room, web) — delete this folder together with the Room part of :data.
//
// Web worker that runs SQLite (the official WASM build) for Room's WebWorkerSQLiteDriver on the web
// target. It implements the driver's message protocol (open, prepare, step, close; see the KDoc of
// androidx.sqlite.driver.web.WebWorkerSQLiteDriver) and stores databases in the browser's Origin
// Private File System through the "opfs-sahpool" VFS, which needs no special HTTP headers. Where
// OPFS is unavailable (old browsers, some private windows) it falls back to in-memory databases.
//
// Based on the reference worker in https://github.com/danysantiago/room-web-demo (Apache-2.0).
import sqlite3InitModule from '@sqlite.org/sqlite-wasm';

let sqlite3 = null;
let opfsPool = null;

// Open database connections and prepared statements by their unique ids.
const databases = new Map();
const statements = new Map();
let nextDatabaseId = 0;
let nextStatementId = 0;

/** Sends a successful response; `error` is always present so the driver can read it as a property. */
function respond(id, data) {
    postMessage({ id: id, data: data, error: null });
}

/** Sends an error response for the request [id]. */
function fail(id, message) {
    postMessage({ id: id, data: null, error: message });
}

/** Opens a database file in OPFS, or an in-memory database when OPFS is unavailable. */
function openRequest(id, request) {
    try {
        const database = opfsPool
            ? new opfsPool.OpfsSAHPoolDb('/' + request.fileName)
            : new sqlite3.oo1.DB(':memory:', 'c');
        const databaseId = nextDatabaseId++;
        databases.set(databaseId, database);
        respond(id, { databaseId: databaseId });
    } catch (error) {
        fail(id, error.message);
    }
}

/** Prepares a statement and reports its parameter count and column names. */
function prepareRequest(id, request) {
    const database = databases.get(request.databaseId);
    if (!database) {
        fail(id, 'Invalid database id: ' + request.databaseId);
        return;
    }
    try {
        const statement = database.prepare(request.sql);
        const statementId = nextStatementId++;
        statements.set(statementId, statement);
        const columnNames = [];
        for (let i = 0; i < statement.columnCount; i++) {
            columnNames.push(sqlite3.capi.sqlite3_column_name(statement, i));
        }
        respond(id, {
            statementId: statementId,
            parameterCount: sqlite3.capi.sqlite3_bind_parameter_count(statement),
            columnNames: columnNames,
        });
    } catch (error) {
        fail(id, error.message);
    }
}

/** Binds the parameters, runs the statement, and returns every result row with the column types. */
function stepRequest(id, request) {
    const statement = statements.get(request.statementId);
    if (!statement) {
        fail(id, 'Invalid statement id: ' + request.statementId);
        return;
    }
    try {
        const rows = [];
        const columnTypes = [];
        statement.reset();
        statement.clearBindings();
        for (let i = 0; i < request.bindings.length; i++) {
            statement.bind(i + 1, request.bindings[i]);
        }
        while (statement.step()) {
            if (columnTypes.length === 0) {
                for (let i = 0; i < statement.columnCount; i++) {
                    columnTypes.push(sqlite3.capi.sqlite3_column_type(statement, i));
                }
            }
            rows.push(statement.get([]));
        }
        respond(id, { rows: rows, columnTypes: columnTypes });
    } catch (error) {
        fail(id, error.message);
    }
}

/** Closes a statement or a database connection. One-way: only failures are answered. */
function closeRequest(id, request) {
    try {
        if (request.statementId != null && statements.has(request.statementId)) {
            statements.get(request.statementId).finalize();
            statements.delete(request.statementId);
        }
        if (request.databaseId != null && databases.has(request.databaseId)) {
            databases.get(request.databaseId).close();
            databases.delete(request.databaseId);
        }
    } catch (error) {
        fail(id, error.message);
    }
}

const handlers = {
    open: openRequest,
    prepare: prepareRequest,
    step: stepRequest,
    close: closeRequest,
};

/** Validates a request message and dispatches it to the handler of its command. */
function handleMessage(event) {
    const message = event.data;
    if (message == null || message.data == null || message.data.cmd == null) {
        fail(message ? message.id : null, "Invalid request: missing 'data' or 'cmd'");
        return;
    }
    const handler = handlers[message.data.cmd];
    if (handler) {
        handler(message.id, message.data);
    } else {
        fail(message.id, "Unknown command: '" + message.data.cmd + "'");
    }
}

// Requests that arrive while SQLite is still loading are queued and handled once it is ready.
const pending = [];
onmessage = (event) => {
    if (sqlite3) {
        handleMessage(event);
    } else {
        pending.push(event);
    }
};

sqlite3InitModule()
    .then(async (instance) => {
        try {
            opfsPool = await instance.installOpfsSAHPoolVfs({});
        } catch (error) {
            console.warn('OPFS is not available, databases are kept in memory only: ' + error.message);
        }
        sqlite3 = instance;
        while (pending.length > 0) {
            handleMessage(pending.shift());
        }
    })
    .catch((error) => console.error('SQLite failed to load: ' + error.message));
