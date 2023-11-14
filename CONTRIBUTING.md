This file is only intended for people working on constructing the workshop. 

The most important thing is to keep the branches clean, and the code in the right place at all times. 
Main should contain all information about all modules, and only the starting template for module 0.

Each module start and end are marked by a branch.

For example, the start of module 1 is marked by the branch `module-1-start`, and the end of module 1 is marked by the branch `module-1-end`.

The order of branches is as follows :

-> main
    -> module-0-start (identical to main)
        -> module-0-end
            -> module-1-start
                -> module-1-end
                    -> module-2-start (identical to module-1-end-branch)
                        -> module-2-end
                            -> module-3-start
                                -> module-3-end
                                    -> module-4-start
                                        -> module-4-end


And merges should follow that flow!