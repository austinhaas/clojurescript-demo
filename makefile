# dependencies
#  java

#===============================================================================
# Config (that a user might actually change)

clojure-tools-version := 1.11.1.1149

config-dir := config

#-------------------------------------------------------------------------------
# Config (that is automatically set up)

os := $(shell uname)

clojure-tools-script := linux-install-$(clojure-tools-version).sh

clj := ./clojure/bin/clj
clojure := ./clojure/bin/clojure

#===============================================================================
# Targets

#-------------------------------------------------------------------------------
# Installation

.PHONY: install-clojure-tools
install-clojure-tools : ## Install the Clojure CLI tools locally in this project directory.
	curl -O https://download.clojure.org/install/$(clojure-tools-script)
ifeq ($(os), Darwin)
	sed -i '' -E 's/^install/ginstall/g' $(clojure-tools-script)
endif
	chmod +x $(clojure-tools-script)
	./$(clojure-tools-script) --prefix clojure
	rm $(clojure-tools-script)

.PHONY: uninstall-clojure-tools
uninstall-clojure-tools : ## Uninstall Clojure CLI tools that were installed locally.
	rm -rf clojure

#-------------------------------------------------------------------------------
# Test

.PHONY: test-clj
test-clj : ## Run unit tests with the Clojure test runner.
	$(clojure) -M:test:clj-runner

.PHONY: test-cljs
test-cljs : ## Run unit tests with the ClojureScript test runner.
	$(clojure) -M:test:cljs-runner

.PHONY: test
test : test-clj test-cljs ## Run unit tests with both the Clojure and ClojureScript test runners.

#-------------------------------------------------------------------------------
# Dev/Build

.PHONY: dev-build
dev-build : ## Build once with dev settings.
	$(clojure) \
	-M \
	--main cljs.main \
	--compile-opts $(config-dir)/dev.edn \
	--compile

.PHONY: dev-watch
dev-watch : ## Build with dev settings and watch src for changes.
	$(clojure) \
	-M \
	--main cljs.main \
	--watch src \
	--compile-opts $(config-dir)/dev.edn \
	--compile

.PHONY: dev-repl
dev-repl : ## Build with dev settings, watch src for changes, start REPL, and open browser
	$(clojure) \
	-M \
	--main cljs.main \
	--watch src \
	--compile-opts $(config-dir)/dev.edn \
	--compile \
	--repl

.PHONY: dev-proc
dev-proc : ## Build with dev settings, launch a REPL socket, and watch src for changes.
	$(clj) \
	-M:socket \
	--main cljs.main \
	--watch src \
	--compile-opts $(config-dir)/dev.edn \
	--compile

.PHONY: opt-build
opt-build : ## Build once with optimized settings.
	$(clojure) \
	-M \
	--main cljs.main \
	--compile-opts $(config-dir)/optimized.edn \
	--compile

.PHONY: opt-watch
opt-watch : ## Build with optimized settings and watch src for changes.
	$(clojure) \
	-M \
	--main cljs.main \
	--watch src \
	--compile-opts $(config-dir)/optimized.edn \
	--compile

#-------------------------------------------------------------------------------
# Other

.PHONY: clj-repl-server
clj-repl-server : ## Start a REPL server.
	$(clojure) -M:clj-repl-server

pom.xml : deps.edn ## Generate pom.xml.
	clj -Spom

.PHONY: display-dependency-updates
display-dependency-updates : pom.xml ## Report on stale dependencies
	mvn versions:display-dependency-updates

.PHONY: serve
serve : ## Launch a webserver (requires python3)
	python3 -m http.server 4000

.PHONY: clean
clean :
	rm -rf .cljs-test-runner-out
	rm -rf .cljs_node_repl
	rm -rf .cpcache
	rm -rf out
	rm -rf target
	rm -f pom.xml

.DEFAULT_GOAL := help

help:
	@grep -E '^[a-zA-Z_-]+ ?:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
