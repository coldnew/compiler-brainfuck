(ns coldnew.compiler.brainfuck.parser-spec
  (#?(:clj  :require
      :cljs :require-macros)
   [speclj.core :refer [describe it should should-not]])
  (:require [speclj.run.standard :refer [run-specs]]
            [coldnew.compiler.brainfuck.parser :as parser]))


(describe
 "Testing parser"

 (it "remove-comments: should remove all chars not in '<>-+,.[]' and return string."
     (should (= "" (parser/remove-comments "abcdefghijklmnopqrstuvwxyz")))
     (should (= "[]" (parser/remove-comments "abcde[fghijklmno]pqrstuvwxyz")))
     (should (= "[]" (parser/remove-comments "[This is comment line]")))
     (should (= "[]<>-+,.[]" (parser/remove-comments "[This is comment line]<>-+,.[]"))))

 (it "tokenize: should convert string to character sequence."
     (should (= nil (parser/tokenize "")))
     (should (= [\a \b \c] (parser/tokenize "abc"))))

 (it "parse: should convert brainfuck code string to AST."
     (should (= [\, [\> \,]] (parser/parse ",[>,]")))
     (should (= [[]] (parser/parse "x[]")))
     (should (= [[\- \> \+ \<]] (parser/parse "[->+<]")))))