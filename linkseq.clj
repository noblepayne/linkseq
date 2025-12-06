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

   :bio {:tagline "Software Engineer & Podcaster"
         :tags ["Distributed Systems" "Podcasting 2.0" "Clojure" "Nix"]}

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

(defn render-bio-tags [tags]
  [:p.bio.tags
   (interpose [:span.separator "|"]
              (map (fn [tag] [:span.tag tag]) tags))])

(defn page [data]
  (h/html
   (h/raw "<!DOCTYPE html>")
   [:html {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     [:meta {:name "description" :content (get-in data [:bio :tagline])}]
     [:title (:title data)]
     [:link {:href "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
             :rel "stylesheet"}]
     [:style css]]
    [:body
     [:main#main
      [:header
       [:img.avatar {:src (:avatar data) :alt (:title data)}]
       [:h1 (:title data)]
       [:p.bio (get-in data [:bio :tagline])]
       (render-bio-tags (get-in data [:bio :tags]))]

      [:nav#links {:role "navigation" :aria-label "Main links"}
       (for [{:keys [url label icon color]} (:links data)]
         [:a.link-card
          {:href url
           :target "_blank"
           :rel "noopener noreferrer"
           :data-brand (name color)}
          (when icon [:span.link-icon (render-icon icon)])
          [:span.link-title label]])]

      [:div#socials {:role "navigation" :aria-label "Social links"}
       (for [{:keys [url icon color]} (:socials data)]
         [:a.social-link
          {:href url
           :target "_blank"
           :rel "noopener noreferrer"
           :aria-label (name icon)
           :data-brand (name color)}
          (render-icon icon)])]

      [:footer
       [:a.source-link
        {:href "https://github.com/noblepayne/linkseq"
         :target "_blank"
         :rel "noopener noreferrer"}
        "source"]]]]]))

;; ============================================================
;; MAIN
;; ============================================================

(defn -main []
  (let [out-dir (io/file "public")]
    (.mkdirs out-dir)
    (spit (io/file out-dir "index.html") (str (page site)))
    (println "✅ Generated public/index.html")))

(-main)
