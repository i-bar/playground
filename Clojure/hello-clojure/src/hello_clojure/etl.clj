(ns hello-clojure.etl
  (:require [clojure.string :as str])
  (:require [clj-time.core :as time])
  (:require [clj-time.format :as tf])
  (:require [clojure.data.csv :as csv]))


(def in-file "humans.csv")
(def out-file "humans.json")



(defn extract-age
  [age-str]
  (zipmap (map keyword (re-seq #"[a-z]+" age-str))
          (map read-string (re-seq #"\d" age-str))))

; (extract-age " 1 years,   5 days, 3 months")
; {:years 1, :days 5, :months 3}


(defn extract
  [file]
  (->> (csv/read-csv (slurp file))
       (map (fn [[n s a]] [n s (extract-age a)]))))

(extract filename)
;; (["Edward" "Cullen" {:years 1, :months 0, :days 9}] ...


(defn valid-name
  [name]
  (some? (re-matches #"[A-Za-z \\-]+" name)))

(defn valid-age
  [age]
  (or (some? (:years age))
      (some? (:months age))
      (some? (:days age))))

(defn validate
  [humans]
  (filter (fn 
            [[name surname age]]
            (and (valid-name name)
                 (valid-name surname)
                 (valid-age age)))
          humans))


(defn fill-nil
  [val]
  (if (nil? val) 0 val))

(defn birth-date
  ;; {years: 1, months: 1, days: 1} => Date one year & month & day ago.
  [age]
  (time/minus (time/now)
              (time/years (fill-nil (age :years)))
              (time/months (fill-nil (age :months)))
              (time/days (fill-nil (age :days)))))

(birth-date {:years 1 :months 2 :days 1})
(birth-date {:days 1})
;; (birth-date (mapify-age "1 days, 1 years, 1 months"))

(defn transform-age
  ;; "1 days, 1 years, 1 months" => birthdate = today - 1y, 1m & 1d.
  [age]
  (tf/unparse (tf/formatters :rfc822) (birth-date age)))

;; (transform-age "1 days, 1 years, 1 months")


(defn transform
  [humans]
  (map (fn [[ln fn age]] [ln fn (transform-age age)]) humans))


(defn load-human
  [[name surname birthdate]]
  (str "\t{ \"name\": " name
       ", \"surname\": " surname
       ", \"birthdate\": " birthdate " }"))

(defn load-json
  [out-file humans]
  (spit out-file (str "{ \"humans\": [ \n"
                      (str/join ",\n" (map load-human humans))
                      "\n\t]\n}")))

(defn csv-to-json
  [in-file]
  (->> in-file
       extract
       validate
       transform
       (load-json out-file)))


