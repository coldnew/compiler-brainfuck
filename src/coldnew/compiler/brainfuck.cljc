(ns coldnew.compiler.brainfuck
  (:require [coldnew.compiler.brainfuck.compiler :refer [compile-to]]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            #?(:clj  [clojure.java.io :as io])
            #?(:cljs [cljs.nodejs :as nodejs]))
  (:refer-clojure :exclude [compile])
  #?(:clj (:gen-class)))

(defn compile
  [{:keys [source-code filename target optimize-level] :as options
    :or {optimize-level 0}}]
  (compile-to options))
