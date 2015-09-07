    git clone --recursive .... !

After checkout run

    for i in dpa*; do (cd $i; git checkout master); done

to switch from detached head to something useful.  In IntelliJ the
multi-repository scenario is picked up correctly when committing
and pulling.  

