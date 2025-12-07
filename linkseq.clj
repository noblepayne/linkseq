#!/usr/bin/env bb
(ns linkseq
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [hiccup2.core :as h]))

;; ============================================================
;; CONFIG
;; ============================================================

(def config
  {;; Assets
   :css  "style.css"
   :avatar "assets/avatar.txt"
   :favicon "assets/favicon.txt"
   :icons {:globe     "assets/globe.svg"
           :boostbox  "assets/boostbox.svg"
           :github    "assets/github.svg"
           :podcast   "assets/podcast.svg"
           :linkedin  "assets/linkedin.svg"
           :x         "assets/x.svg"
           :email     "assets/email.svg"}

   ;; Personal Info
   :title "Wes Payne"
   :bio {:tagline "Software Engineer & Podcaster"
         :tags ["Distributed Systems" "Podcasting 2.0" "Clojure" "Nix"]}

   :links [{:label "LinkedIn / CV"            :url "https://www.linkedin.com/in/noblepayne" :icon :linkedin :color :linkedin}
           {:label "LINUX Unplugged"          :url "https://linuxunplugged.com"             :icon :podcast  :color :youtube}
           {:label "Open Source Projects"     :url "https://github.com/noblepayne"          :icon :github   :color :github}
           {:label "BoostBox Metadata Server" :url "https://boostbox.noblepayne.com"        :icon :boostbox :color :teal}]

   :socials [{:url "https://www.linkedin.com/in/noblepayne" :icon :linkedin :color :linkedin}
             {:url "https://github.com/noblepayne"          :icon :github   :color :github}
             {:url "https://x.com/wespayne"                 :icon :x        :color :teal}
             {:url "mailto:wes@noblepayne.com"              :icon :email    :color :email}]
   :source "https://github.com/noblepayne/linkseq"})

;; ============================================================
;; RENDERING
;; ============================================================

(defn read-file [filepath]
  (h/raw (slurp filepath)))

(defn render-icon [icons k]
  (assert (contains? icons k) (str "icon k (" k ") not found in icons: " icons))
  (when k (read-file (get icons k))))

(defn render-bio-tags [tags]
  [:p.bio.tags
   (interpose [:span.separator "|"]
              (map (fn [tag] [:span.tag tag]) tags))])

(defn render [{:keys [:css :favicon :avatar :icons] :as data}]
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
     [:link {:rel "icon" :type "image/png" :href (read-file favicon)}]
     [:style (read-file css)]]
    [:body
     [:main#main
      [:header
       [:img.avatar {:src (read-file avatar) :alt (:title data)}]
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
          (when icon [:span.link-icon (render-icon icons icon)])
          [:span.link-title label]])]

      [:div#socials {:role "navigation" :aria-label "Social links"}
       (for [{:keys [url icon color]} (:socials data)]
         [:a.social-link
          {:href url
           :target "_blank"
           :rel "noopener noreferrer"
           :aria-label (name icon)
           :data-brand (name color)}
          (render-icon icons icon)])]

      [:footer
       [:a.source-link
        {:href (:source data)
         :target "_blank"
         :rel "noopener noreferrer"}
        "source"]]]]]))

;; ============================================================
;; MAIN
;; ============================================================

(defn -main []
  (let [out-dir-prefix "public"
        out-dir (io/file out-dir-prefix)]
    (.mkdirs out-dir)
    (spit (io/file out-dir "index.html") (render config))
    (println (str "✅ Generated " out-dir-prefix "/index.html"))))

(-main)
