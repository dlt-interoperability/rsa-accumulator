# Coding Conventions

This codebase uses functional programming principles as much as possible. A
functional library for Kotlin, called [Arrow](https://arrow-kt.io/docs/core/) is
used, primarily for error handling with the `Either` type.

Conventions:

- Explicit state management using immutable state and the State monad to model
  state changes.
- Catch exceptions as close as possible to their source and convert to [Arrow's
  `Either`
  type](https://arrow-kt.io/docs/apidocs/arrow-core-data/arrow.core/-either/).
- Implement functions as expressions. Flows that produce errors can be composed
  using `map`, `flatMap` and `fold`. Avoid statements with side effects in
  functions.
- Use recursion over loops (when tail recursion is possible to improve
  performance and avoid stack overflow).

Example Gists:

- [How to catch exceptions and convert to and Either
  type](https://gist.github.com/airvin/79f1fb2a3821a9e5d227db3ee9561f42).
- [Using flatMap to compose functions that return
  Eithers](https://gist.github.com/airvin/3bfae1f3e622e466ba9072b53684555a).
- [Folding over an Either Error to reduce to a single
  type](https://gist.github.com/airvin/eabc99a9552a0573afd2dd9a13e75948).
