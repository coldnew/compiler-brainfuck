(ns coldnew.compiler.brainfuck
  (:require [coldnew.compiler.brainfuck.compiler :refer [compile-to]]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            #?(:clj  [clojure.java.io :as io])
            #?(:cljs [cljs.nodejs :as nodejs]))
  (:refer-clojure :exclude [compile])
  #?(:clj (:gen-class)))

(defn compile
  [{:keys [source-code filename target optimize] :as options
    :or {optimize 0}}]
  (compile-to options))

;; 
;; Functions for work as standalone application

(defn- read-file [file]
  #?(:clj
     (slurp file)
     :cljs
     (let [fs (nodejs/require "fs")]
       (.readFileSync fs file "utf8"))))

(defn- write-file [file data]
  #?(:clj
     (spit file data)
     :cljs
     (let [fs (nodejs/require "fs")]
       (.writeFileSync fs file data "utf8"))))

(defn- do-compile [options]
  (println "Input:  " (:input-file options))
  (println "Output: " (:output-file options))
  (println "Target: " (name (:target options)))
  (println "Optimize Level: " (:optimize-level options))

  (write-file (:output-file options)
              (compile {:source-code (read-file (:input-file options))
                        :target      (:target     options)
                        :optimize    (:optimize-level options)})))

(def ^:private cli-options
  ;; An option with a required argument
  [;; ["-i" "--input-file  INPUT"]
   ["-o" "--output-file OUTPUT"]
   ["-t" "--target LANGUAGE"      "Target language"
    :default :c
    :parse-fn #(keyword %)
    :validate [#(contains? #{:c :java :nodejs :python :rust} %) "Unsupported target"]]
   ["-O" "--optimize-level LEVEL"
    :default 0
    :parse-fn #?(:clj  #(Integer/parseInt %)
                 :cljs #(js/parseInt %))
    :validate [#(< 0 % 3) "Must be a number in 0 ~ 2 (inclusive)."]]
   ;; A boolean option defaulting to nil
   ["-h" "--help"]])

(defn- usage [options-summary]
  (->> [""
        "Usage: bfc [options] file"
        ""
        "Options:"
        options-summary
        ""
        "Optimize level:"
        "  0     No optimize. (default)"
        "  1     Remove consecutive terms."
        "  2     Remove common idiom."
        ""
        "Target language:"
        "  c          (default)"
        "  java"
        "  nodejs"
        "  python"
        "  rust"
        ""]
       (str/join \newline)))

(defn- error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn- exit [status msg]
  (println msg)
  #?(:clj  (System/exit status)
     :cljs (.exit nodejs/process status)))

;; enable *print-fn* in clojurescript
#?(:cljs (enable-console-print!))

(defn- file-exists? [path]
  (let [msg (str "ERROR: file " path " not found!!")]
    #?(:clj
       (when-not (.exists (io/as-file path))
         (exit 1 msg))
       :cljs
       (let [fs (nodejs/require "fs")]
         (try
           (.accessSync fs path fs.F_OK)
           (catch js/Error _
             (exit 1 msg)))))))


(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    ;; Handle help and error conditions
    (cond
      (:help options) (exit 0 (usage summary))
      (< (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))

    (let [input-file  (get arguments 0)
          output-file (or (:output-file options) (get arguments 1))]

      ;; Check if input file exist or not
      (file-exists? input-file)

      ;; Time to compile branfuck sources
      (do-compile {:input-file       input-file
                   :output-file      output-file
                   :target           (:target options)
                   :optimize-level   (:optimize-level options)}))))

;; setup node.js starter point
#?(:cljs (set! *main-cli-fn* -main))