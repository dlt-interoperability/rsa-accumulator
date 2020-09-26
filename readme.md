# RSA Accumulator

This is a Kotlin implementation of an RSA accumulator, originally described by
Benaloh and de Mar [1]. An accumulator is a cryptographic primitive that
generates a succinct digest of elements in a set in a way that allows for proof
of membership (called a witness) of an element in the set without revealing
other information about the set.

This library implements a _dynamic_ RSA accumulator, meaning the accumulator can
be efficiently updated when elements are added and removed from the set [2].
"Efficiently", in this case, means independent of the size of the accumulated set.
The method for performing dynamic updates uses auxiliary information about the
accumulator, as described by Li et. al. [3].

This library is intended to be used to allow for verifiable observation of state
from a blockchain, where the accumulator represents a digest of the entire
ledger. It is therefore expected that for every block produced, multiple
elements will need to be added and removed from the accumulated set. Because of
this, batch updates has also been implemented in this library [4].

# Building

The project can be built with:

```
./gradlew build
```

# Coding Conventions

This library uses functional programming principles whereever possible. For a
detailed overview of the conventions see the [Coding
Conventions](coding-conventions.md) document.

## References

- [1] Benaloh and de Mar, 1994
- [2] Camenisch and Lysyanskaya, 2002
- [3] Li, Li, Xue, 2007
- [4] Boneh, Bunz, Fisch, 2019
