    git clone --recursive .... !

After checkout run

    git -C dpaviser-metadata-checker checkout master
    git -C dpaviser-qa-tool checkout master
    git -C dpaviser-structure-checker checkout master
    git -C jhove checkout integration
    git -C newspaper-batch-event-framework checkout dpaviser-statistics-refactoring

to switch from detached head to something useful for our own projects.

Additional projects have been added at very specific commits to ensure the build can
bootstrap without $HOME/.m2/settings.xml being configured, i.e. without knowledge of sbforge.

In IntelliJ the multi-repository scenario is picked up correctly when committing
and pulling.  

