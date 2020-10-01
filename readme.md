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

- [1](https://link.springer.com/content/pdf/10.1007%2F3-540-48285-7_24.pdf)
  Benaloh, J. and de Mar, M., 1994, One-Way Accumulators: A Decentralized
  Alternative to Digital Signatures, International Conference on the Theory and
  Applications of Cryptographic Techniques, vol 765, pp 274-285
- [2](https://groups.csail.mit.edu/cis/pubs/lysyanskaya/cl02a.pdf) Camenisch, J. and Lysyanskaya, A., 2002, Dynamic accumulators and
  application to efficient revocation of anonymous credentials, Lecture Notes in
  Computer Science (including subseries Lecture Notes in Artificial Intelligence
  and Lecture Notes in Bioinformatics), vol 2442, pp 61-76
- [3](https://www.cs.purdue.edu/homes/ninghui/papers/accumulator_acns07.pdf) Li,
  J., Li, N., Xue, R., 2007, Universal accumulators with efficient nonmembership
  proofs, Lecture Notes in Computer Science (including subseries Lecture Notes
  in Artificial Intelligence and Lecture Notes in Bioinformatics), vol 4521, pp
  253-269
- [4](https://eprint.iacr.org/2018/1188.pdf) Boneh, D., Bunz, B., Fisch, B.,
  2019, Batching Techniques for Accumulators with Applications to IOPs and
  Stateless Blockchains, Lecture Notes in Computer Science (including subseries
  Lecture Notes in Artificial Intelligence and Lecture Notes in Bioinformatics),
  vol 11692, pp 561-586

## TODO

- Write some notes on the selection of the RSA modulus size. How does this
  relate to security?
- Write some notes on the prime certainty constant used.
- Delete function using the trapdoor function
