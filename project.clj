(defproject coldnew/compiler.brainfuck "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/clojurescript "1.9.198"]]

  :source-paths ["src"]
  :test-paths ["spec"]

  :plugins [[speclj "3.3.2"]
            [lein-cljsbuild "1.1.3"]]

  :aot [coldnew.compiler.brainfuck]
  :main ^:skip-aot coldnew.compiler.brainfuck

  :jvm-opts ^:replace ["-Dclojure.compiler.direct-linking=true"]

  :profiles {:dev {:dependencies [[speclj "3.3.2"]]}
             :uberjar {:source-paths ["src"]
                       :omit-source true
                       :aot :all}}

  :cljsbuild {:builds {:dev  {:source-paths ["src"]
                              :compiler     {:target :nodejs
                                             :output-to "target/bfc.js"
                                             :optimizations :simple}}
                       :prod {:source-paths  ["src"]
                              :compiler      {:target :nodejs
                                              :output-to "target/bfc.js"
                                              :optimizations :advanced}}}})
