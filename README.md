    git clone --recursive .... !

After checkout run

    for i in dpa*; do (cd $i; git checkout master); done

to switch from detached head to something useful for our own projects.
Additional projects have been added at very specific commits to ensure the build can
run on its own without access to sbforge.

In IntelliJ the multi-repository scenario is picked up correctly when committing
and pulling.  

