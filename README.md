# fpclj-finances

A simple Clojure REST API application for personal finances management.

It's a sample program from the book ["Programação funcional: Uma introdução em Clojure"](https://www.casadocodigo.com.br/products/livro-programacao-funcional-clojure)


## Installation

Install [Leiningen](https://leiningen.org/) and clone this application source code.

## Running

Start the application web server with:

    lein ring server

The endpoints will be accessible at:

    http://localhost:3000/

## Examples

FIXME: Put some examples 

## Tests

Tests use [Midje](https://github.com/marick/Midje). Run all tests with:

    lein midje

You can filter specific tests to run with :filters.

To run only acceptance tests:

    lein midje :filters acceptance

To run all but acceptance tests:

    lein midje :filters -acceptance

To keep listening for changes and executing tests use:

    lein midje :autotest

## Others

Generate a code coverage report with [Cloverage](https://github.com/cloverage/cloverage). Run:

    lein cloverage --runner :midje

You can check the results in fpclj-finances/target/coverage/index.html

---

Generate the application uberjar with:

    lein ring uberjar

This will generate the file fpclj-finances.jar in target directory

---

Check and apply code formatting with [cljfmt](https://github.com/weavejester/cljfmt).
Check with:

    lein cljfmt check

Apply formatting to source code:

    lein cljfmt fix

## TODO's

- Code still needs lots of refactor
- Improve de JSON parsing that is spread in the code
- Expense and Revenue could be Debt and Credit?
