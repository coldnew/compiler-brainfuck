(ns coldnew.compiler.brainfuck.compiler
  (:require [coldnew.compiler.brainfuck.parser           :refer [parse]]
            [coldnew.compiler.brainfuck.ir               :refer [ast->ir]]
            [coldnew.compiler.brainfuck.optimize         :refer [optimize]]
            ;; backends
            [coldnew.compiler.brainfuck.backend.c        :refer [ir->c]]
            [coldnew.compiler.brainfuck.backend.python   :refer [ir->python]]
            [coldnew.compiler.brainfuck.backend.java     :refer [ir->java]]
            [coldnew.compiler.brainfuck.backend.nodejs   :refer [ir->nodejs]]
            [coldnew.compiler.brainfuck.backend.rust     :refer [ir->rust]]
            [coldnew.compiler.brainfuck.backend.go       :refer [ir->go]]
            [coldnew.compiler.brainfuck.backend.csharp   :refer [ir->csharp]]
            ))

(defn parse->ir
  "Build the input OPTIONS to ir format, which work as clojure's data structure.
  The OPTIONS must be a hash-map and contains following keys/values:
  {:input-file     \"xxx.bf\"
   :output-file    \"xxx.c\"
   :target          :c
   :optimize-level  2}"
  [options]
  (-> (:source-code options)
      parse
      ast->ir
      (optimize (:optimize-level options))))

(declare compile-to)

(defmulti compile-to :target)

(defmethod compile-to :default
  [options]
  (throw (ex-info "Unsupported target " {:target {:target options}})))

(defmethod compile-to :c
  [options]
  (->> options parse->ir ir->c))

(defmethod compile-to :python
  [options]
  (->> options parse->ir ir->python))

(defmethod compile-to :java
  [options]
  (->> options parse->ir ir->java))

(defmethod compile-to :nodejs
  [options]
  (->> options parse->ir ir->nodejs))

(defmethod compile-to :rust
  [options]
  (->> options parse->ir ir->rust))

(defmethod compile-to :go
  [options]
  (->> options parse->ir ir->go))

(defmethod compile-to :csharp
  [options]
  (->> options parse->ir ir->csharp))
