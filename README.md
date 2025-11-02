# Minimal repro for failure in clojure.tools.build.api/create-basis on MS-Windows

This repo shows a minimal case where create-basis fails on Windows only when:

- `deps.edn` includes `ring/ring-jetty-adapter`
- the `:build` alias depends on `slipset/deps-deploy`
- the local Maven repository starts empty (`~/.m2`)

To reproduce locally on Windows:
1. Remove your local Maven repo at `C:\Users\<username>\.m2`.
2. Run 
```ps1
clojure -T:build issue
``` 
This calls `build.clj/issue`, which executes:
```clojure
(b/create-basis {:project "deps.edn"})
```

It fails with a `java.nio.file.AccessDeniedException` exception, `:cause C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom.5821626342674319777.tmp -> C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom`:

```clojure
Downloading: org/clojure/clojure/1.12.0/clojure-1.12.0.pom from central
Downloading: io/github/clojure/tools.build/0.10.11/tools.build-0.10.11.pom from central
Downloading: org/clojure/pom.contrib/1.3.0/pom.contrib-1.3.0.pom from central
Downloading: slipset/deps-deploy/0.2.2/deps-deploy-0.2.2.pom from clojars
...
Creating basis...
Downloading: ring/ring-jetty-adapter/1.15.2/ring-jetty-adapter-1.15.2.pom from clojars
...
Downloading: org/eclipse/jetty/ee9/websocket/jetty-ee9-websocket-jetty-server/12.1.0/jetty-ee9-websocket-jetty-server-12.1.0.pom from central
:exception #error {
 :cause C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom.5821626342674319777.tmp -> C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom
 :via
 [{:type org.eclipse.aether.resolution.ArtifactDescriptorException
   :message Failed to read artifact descriptor for org.eclipse.jetty:jetty-server:jar:12.1.0
   :at [org.apache.maven.repository.internal.DefaultArtifactDescriptorReader loadPom DefaultArtifactDescriptorReader.java 305]}
  {:type org.apache.maven.model.resolution.UnresolvableModelException
   :message Could not transfer artifact org.eclipse.jetty:jetty-project:pom:12.1.0 from/to central (https://repo1.maven.org/maven2/): C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom.5821626342674319777.tmp -> C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom
   :at [org.apache.maven.repository.internal.DefaultModelResolver resolveModel DefaultModelResolver.java 176]}
  {:type org.eclipse.aether.resolution.ArtifactResolutionException
   :message Could not transfer artifact org.eclipse.jetty:jetty-project:pom:12.1.0 from/to central (https://repo1.maven.org/maven2/): C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom.5821626342674319777.tmp -> C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom
   :at [org.eclipse.aether.internal.impl.DefaultArtifactResolver resolve DefaultArtifactResolver.java 506]}
  {:type org.eclipse.aether.transfer.ArtifactTransferException
   :message Could not transfer artifact org.eclipse.jetty:jetty-project:pom:12.1.0 from/to central (https://repo1.maven.org/maven2/): C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom.5821626342674319777.tmp -> C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom
   :at [org.eclipse.aether.connector.basic.ArtifactTransportListener transferFailed ArtifactTransportListener.java 52]}
  {:type java.nio.file.AccessDeniedException
   :message C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom.5821626342674319777.tmp -> C:\Users\ikappaki\.m2\repository\org\eclipse\jetty\jetty-project\12.1.0\jetty-project-12.1.0.pom
   :at [sun.nio.fs.WindowsException translateToIOException WindowsException.java 89]}]
 :trace
 [[sun.nio.fs.WindowsException translateToIOException WindowsException.java 89]
  [sun.nio.fs.WindowsException rethrowAsIOException WindowsException.java 103]
  [sun.nio.fs.WindowsFileCopy move WindowsFileCopy.java 317]
  [sun.nio.fs.WindowsFileSystemProvider move WindowsFileSystemProvider.java 293]
  [java.nio.file.Files move Files.java 1432]
  [org.eclipse.aether.util.FileUtils$2 move FileUtils.java 121]
  [org.eclipse.aether.connector.basic.BasicRepositoryConnector$GetTaskRunner runTask BasicRepositoryConnector.java 491]
  [org.eclipse.aether.connector.basic.BasicRepositoryConnector$TaskRunner run BasicRepositoryConnector.java 383]
  [org.eclipse.aether.util.concurrency.RunnableErrorForwarder lambda$wrap$0 RunnableErrorForwarder.java 73]
...
  [org.apache.maven.repository.internal.DefaultArtifactDescriptorReader loadPom DefaultArtifactDescriptorReader.java 296]
  [org.apache.maven.repository.internal.DefaultArtifactDescriptorReader readArtifactDescriptor DefaultArtifactDescriptorReader.java 171]
  [org.eclipse.aether.internal.impl.DefaultRepositorySystem readArtifactDescriptor DefaultRepositorySystem.java 286]
  [clojure.tools.deps.extensions.maven$read_descriptor invokeStatic maven.clj 115]
  [clojure.tools.deps.extensions.maven$read_descriptor invoke maven.clj 106]
...
  [java.lang.Thread run Thread.java 833]]}
```
