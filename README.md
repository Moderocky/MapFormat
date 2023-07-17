Map Format
=====

### Description

A resource for formatting a string with a key <-> value map of inputs.

### Motivation

Java's built-in string format supports (very limited) inputs of strings or objects.
There are other libraries for formatting using a key but these (e.g. Sun Microsystems's 'MapFormat') are usually very
slow, not particularly good at identifying what's going on, or only support one limited key format.

I made this as a tiny, no-regex alternative.

The formatting is done in two passes: the first chops the string up into chunks of literal text and tokens to be read
from the map, and the second rebuilds the string by asking for the values from the map and inserting them into the
string builder.
