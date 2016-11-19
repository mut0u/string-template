(ns string-template.core-test
  (:require [clojure.test :refer :all]
            [string-template.core :refer :all]))



(comment
  (let [id 1 v ["aa" "bb"]]
    (sql-readers @"select * from users where id = #{id} "
                 (if true
                   @"id = 5"
                   @"id = 6"
                   )
                 @"and i in #[v] and active = true")))
