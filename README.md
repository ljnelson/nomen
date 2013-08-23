<!-- -*- markdown -*- -->
# `nomen`

## Intelligent Names for Java

### August 23, 2013

### [Laird Nelson][1]

`nomen` is a small project that models names for parties.

Many database applications require the storage and processing of human
and organizational names.  Frequently, names are effectively
templates built out of other names that are more atomic in nature.
For example, we like to say that a person typically has a first name,
a middle name, a last name, and a full name&mdash;which is effectively
a template built from the other three name types.

Of course, when we say this, we are wrong for at least some subset of
people.  Or we are wrong in our usage of the names themselves.  For
example, some people prefer to go by their last or family names.
Other times people have only one name.  Other times we use these
strangely-designated names for sorting purposes (we tend to sort by
last name, and when we capture a last name we are typically capturing
it for sorting purposes).

When you examine naming deeply, you discover that designating types of
names is flawed unless those name types describe at least indirectly
what someone intends to do with the name so typed.

You also discover that certain names (or name parts) are fixed, or are
drawn from a relatively small finite set of possible values.  For
example, we know that "Jr." is a sequence of three characters that
stands for Junior.

`nomen` treats all these concepts as first-class notions.  A
`NameValue` is a glorified `String` with either an _atomic_ value
("Laird"), or a template value ("${firstName} ${lastName}").  A
`Named` is something that can have a name associated with it.  A
`NameType` is another glorified `String` that identifies the usage of
a given name, and that can be used as a key in templates.  And
finally, a `Name` is a union of a `Named`, a `NameType` and a `Named`,
together with a template evaluation engine.

[1]: http://about.me/lairdnelson
