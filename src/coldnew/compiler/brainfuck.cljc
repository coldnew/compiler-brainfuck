(ns coldnew.compiler.brainfuck
  (:require [coldnew.compiler.brainfuck.compiler :refer [compile-to]]
            [coldnew.compiler.brainfuck.compiler :refer [supported-targets]]
            [clojure.string :as str])
  (:refer-clojure :exclude [compile])
  #?(:clj (:gen-class)))

(defn compile
  [{:keys [source-code filename target optimize-level] :as options
    :or {optimize-level 0}}]
  (compile-to options))

(defn valid-target?
  "Validate target is supported in brainfuck compiler or not."
  [target]
  (contains? supported-targets target))
