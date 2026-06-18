# HEART 2026 Tydi tutorial

This repository contains a project with development container that you can open in VS Code. With this set-up, you can easily write and edit code, and execute the tools of the Tydi ecosystem on those files. Once you clone the project and open the folder in VS Code, it will detect the dev container, and ask if you want to reopen the folder in the dev container. If you do this, the files will live in the container, and the terminal will execute commands within the container, meaning that all CLI tools are available. GUI apps may work from the terminal. A browser-based noVNC interface with a lightweight XFCE desktopenvironment is also exposed on http://localhost:6080/vnc.html. This may be more intuitive for some users. The resolution of this virtual desktop may be changed in the [devcontainer.json](.devcontainer/devcontainer.json) file.

> [!NOTE]
> On Windows, starting the dev container may fail if WSL integration is not enabled in the Docker desktop settings (see Resources tab). An alternate solution may be turning off "Mount Wayland Socket" in VS Code's settings.

Documenation on the container may be found on its [Docker Hub page](https://hub.docker.com/r/hdltypetech/tydi-tools), or its [GitHub repository](https://github.com/abs-tudelft/Tydi-tools).
