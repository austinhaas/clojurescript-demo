# clojurescript-demo

My absolutely minimal foundation for a ClojureScript project on Linux/Mac.

## Dependencies

* Java
* [Clojure Tools](https://clojure.org/releases/tools)
* Python 3 (optional)

## Installation

### 1. Clone this repo
`git clone clojurescript-demo`
### 2. cd into the project directory
`$ cd clojurescript-demo`
### 3. Install the Clojure CLI tools, if you don't have them installed already.
I like to install the tools in the project directory, mostly because it makes it easier (for me) to keep them up to date.
`make install clojure-tools`

## Config

* makefile
* deps.edn
* config

## Usage

The included makefile provides several useful commands. Run `make` for a list of commands and descriptions.

### Development

#### Workflow #1 (what I use)

This command will:
* compile with dev settings
* start a REPL socket server
* start a webserver
* recompile if `src` changes

```
$ make dev-proc
```
Connect to the REPL socket server on port 5555.

In the REPL, which should be in the user ns, eval `(browser-repl)`.

Open a browser to localhost:9000.

Open browser console.

You should see "ClojureScript demo project started"

#### Workflow #2 (what might be easiest to get started)

This command will:
* compile with dev settings
* start a REPL
* start a webserver
* open a browser tab to point to the webserver
* recompile if `src` changes

```
make dev-repl
```
Open browser console.

You should see "ClojureScript demo project started"

## License

Copyright © 2021 Austin Haas
Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
