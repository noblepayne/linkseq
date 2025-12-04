#!/usr/bin/env bb
(ns render
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [hiccup2.core :as h]))

;; ============================================================
;; BRAND COLORS & ICONS
;; ============================================================

(def brands
  {:github    "#FF9D6F"
   :linkedin  "#6DD5ED"
   :youtube   "#FF6B9D"
   :x         "#4FC3F7"
   :accent    "#FF7B54"
   :teal      "#5EEAD4"})

(def icons
  {:globe     (slurp "assets/globe.svg")
   :boostbox  (slurp "assets/boostbox.svg")
   :github    (slurp "assets/github.svg")
   :youtube   (slurp "assets/youtube.svg")
   :podcast   (slurp "assets/podcast.svg")
   :linkedin  (slurp "assets/linkedin.svg")
   :x         (slurp "assets/x.svg")})

;; ============================================================
;; CONFIG
;; ============================================================

(def site
  {:title "Wes Payne"
   :avatar (h/raw (slurp "assets/avatar.txt"))

   :bio-lines ["Software Engineer & Podcaster"
               (str/join " <span style='color: #FF7B54'>|</span> "
                         ["Distributed Systems" "Podcasting 2.0" "Clojure" "Nix"])]

   :links [{:label "Open Source Projects"     :url "https://github.com/noblepayne"          :icon :github   :color :github}
           {:label "LinkedIn / CV"            :url "https://www.linkedin.com/in/noblepayne" :icon :linkedin :color :linkedin}
           {:label "LINUX Unplugged"          :url "https://linuxunplugged.com"             :icon :podcast  :color :youtube}
           {:label "BoostBox Metadata Server" :url "https://github.com/noblepayne/boostbox" :icon :boostbox :color :accent}]

   :socials [{:url "https://www.linkedin.com/in/noblepayne" :icon :linkedin :color :linkedin}
             {:url "https://github.com/noblepayne" :icon :github   :color :github}
             {:url "https://x.com/wespayne" :icon :x :color :teal}]})

;; ============================================================
;; CSS
;; ============================================================

(def css (h/raw (slurp "assets/style.css")))

;; ============================================================
;; RENDERING
;; ============================================================

(defn render-icon [k]
  (when k (h/raw (get icons k ""))))

(defn get-color [k]
  (get brands k))

(defn page [data]
  (h/html
   (h/raw "<!DOCTYPE html>")
   [:html {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     [:meta {:name "description" :content (first (:bio-lines data))}]
     [:title (:title data)]
     [:link {:href "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
             :rel "stylesheet"}]
     [:style css]]
    [:body
     [:main#main
      [:header
       [:img.avatar {:src (:avatar data) :alt (:title data)}]
       [:h1 (:title data)]
       (for [line (:bio-lines data)]
         [:p.bio (h/raw line)])]

      [:nav#links {:role "navigation" :aria-label "Main links"}
       (for [{:keys [url label icon color]} (:links data)]
         [:a.link-card
          {:href url
           :target "_blank"
           :rel "noopener noreferrer"
           :style (when color (str "--brand-color:" (get-color color)))}
          (when icon [:span.link-icon (render-icon icon)])
          [:span.link-title label]])]

      [:div#socials {:role "navigation" :aria-label "Social links"}
       (for [{:keys [url icon color]} (:socials data)]
         [:a.social-link
          {:href url
           :target "_blank"
           :rel "noopener noreferrer"
           :aria-label (name icon)
           :style (str "--brand-color:" (get-color color))}
          (render-icon icon)])]

      [:footer
       [:a {:href "https://github.com/noblepayne/linkseq"
            :target "_blank"
            :rel "noopener noreferrer"}
        [:p {:style "font-family: monospace;"} [:i "source"]]]]]]]))

;; ============================================================
;; MAIN
;; ============================================================

(defn -main []
  (let [out-dir (io/file "public")]
    (.mkdirs out-dir)
    (spit (io/file out-dir "index.html") (str (page site)))
    (println "✅ Generated public/index.html")))

(-main)
