This file is only intended for people working on constructing the workshop. 

The most important thing is to keep the tags clean, and the code in the right place at all times. 
Main should contain all information about all modules, and only the starting template for module 0.

Each module start and end are marked by a tag, as well as a living pull request against their direct counterpart.

For example, the start of module 1 is marked by the tag `module-1-start`, and the end of module 1 is marked by the tag `module-1-end`.
There is a pull request open (and never merged) that merges the module-1 branch into the main branch, and another merging module-2 into module-1.

Overriding a tag is not directly possible. To do it, place yourself on the commit you want to tag, and run the following command:
`git tag -f module-1-start`


The order of branches is as follows :

-> main
    -> module-0-start-branch (identical to main)
        -> module-0-end-branch
            -> module-1-start-branch
                -> module-1-end-branch
                    -> module-2-start-branch (identical to module-1-end-branch)
                        -> module-2-end-branch
                            -> module-3-start-branch
                                -> module-3-end-branch


And merges should follow that flow!