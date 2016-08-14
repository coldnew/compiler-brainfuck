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
            ))

(declare compile-to)

(defmulti compile-to :target)

(defmethod compile-to :c
  [options]
  (-> (:source-code options)
      parse
      ast->ir
      (optimize (:optimize-level options))
      ir->c))

(defmethod compile-to :python
  [options]
  (-> (:source-code options)
      parse
      ast->ir
      (optimize (:optimize-level options))
      ir->python))

(defmethod compile-to :java
  [options]
  (-> (:source-code options)
      parse
      ast->ir
      (optimize (:optimize-level options))
      ir->java))

(defmethod compile-to :nodejs
  [options]
  (-> (:source-code options)
      parse
      ast->ir
      (optimize (:optimize-level options))
      ir->nodejs))

(defmethod compile-to :rust
  [options]
  (-> (:source-code options)
      parse
      ast->ir
      (optimize (:optimize-level options))
      ir->rust))
