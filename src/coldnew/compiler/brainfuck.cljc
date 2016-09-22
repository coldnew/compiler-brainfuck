(ns coldnew.compiler.brainfuck
  (:require [coldnew.compiler.brainfuck.compiler :refer [compile-to]]
            [clojure.string :as str])
  (:refer-clojure :exclude [compile])
  #?(:clj (:gen-class)))

(defn compile
  [{:keys [source-code filename target optimize-level] :as options
    :or {optimize-level 0}}]
  (compile-to options))
