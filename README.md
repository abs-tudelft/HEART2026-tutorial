# HEART 2026 Tydi tutorial

This repository contains a project with development container that you can open in VS Code. With this set-up, you can easily write and edit code, and execute the tools of the Tydi ecosystem on those files. Once you clone the project and open the folder in VS Code, it will detect the dev container, and ask if you want to reopen the folder in the dev container. If you do this, the files will live in the container, and the terminal will execute commands within the container, meaning that all CLI tools are available. GUI apps may work from the terminal. A browser-based noVNC interface with a lightweight XFCE desktopenvironment is also exposed on http://localhost:6080/vnc.html. This may be more intuitive for some users. The resolution of this virtual desktop may be changed in the [devcontainer.json](.devcontainer/devcontainer.json) file.

> [!NOTE]
> On Windows, starting the dev container may fail if WSL integration is not enabled in the Docker desktop settings (see Resources tab). An alternate solution may be turning off "Mount Wayland Socket" in VS Code's settings.

Documenation on the container may be found on its [Docker Hub page](https://hub.docker.com/r/hdltypetech/tydi-tools), or its [GitHub repository](https://github.com/abs-tudelft/Tydi-tools).

## Resources

Here are some resources you can check out to get more information about Tydi, Tydi development tools, and the debug tools.

- [Tydi and tools documentation](https://abs-tudelft.github.io/docs/)
- [Tydi stream visualizer web application](https://abs-tudelft.github.io/tydi-stream-vis/)
- [List of Tydi-related publications](https://hdltypetech.com/publications/)
- A mostly up to date list of tools and projects:
    - [Tydi stream visualizer](https://github.com/abs-tudelft/tydi-stream-vis) – Aforementioned web application to build Tydi structures from JSON input data and visualize the streams and packets
    - [TinyTydi](https://gitlab.com/hstruik/tinytydi) – A simulator of transfer building of Tydi streams using formal semantics
    - [Tydi-Chisel](https://github.com/abs-tudelft/Tydi-Chisel) – The Scala library for integrating Tydi concepts inside Chisel
    - [Tywaves](https://github.com/rameloni/tywaves-chisel) – Type-enabled waveform viewing for Chisel
    - [ChiselTrace](https://github.com/jarlb/chiseltrace) – Signal dependency tracing for Chisel designs
    - [Tydi-lang](https://github.com/twoentartian/tydi-lang-2) – Tydi-lang compiler
    - [Tydi-lang-2-Chisel](https://github.com/ccromjongh/tydi-lang-2-chisel) – A Tydi-lang-IR to Chisel transpiler
    - [TIL-JSON](https://github.com/jhaenen/JSON_hierachy) – A tool for automatically generating a JSON to Tydi streams parser  
    _Note: not actively maintained anymore_
    - [TIL](https://github.com/matthijsr/til-vhdl) – The Tydi Intermediate Representation to VHDL compiler  
    _Note: not actively maintained anymore_