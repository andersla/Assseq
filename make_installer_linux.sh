#!/bin/sh
set -e

echo "Make installer fow linux"

#----------------------------------
#prepare install-file for LINUX
#----------------------------------

rsync -av assseq-linux/* target/linux-version*/assseq/

# make sure all files have right permissions
chmod 755 -R target

# move into dir
cd target/linux-version*/assseq/

makeself . ../assseq.install.run "Installer for Assseq" ./install.sh

# move up one step back
cd ..

# make standard archive for linux
tar -czvf assseq.tgz assseq/

# move back
cd ../../

# and linux install instr to package-dir
rsync -av target/linux-version*/assseq/install.readme.txt target/linux-version*
rsync -av htaccess-files/linux-install-dir/.htaccess target/linux-version*

