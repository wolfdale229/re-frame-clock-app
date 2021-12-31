(ns ^:figwheel-hooks learn-reframe.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [re-frame.core :as rf]
   [re-frame.db :as db]))

;; let's make a initialization event
(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:time (js/Date.)
    :time-color "orange"}))

(rf/dispatch-sync [:initialize])

(defn get-time []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))

(defonce time-per-second (js/setInterval get-time 1000))

(rf/reg-event-db
 :timer
 (fn [db [_ new-time]]
   (assoc db :time new-time)))

(rf/reg-event-db
 :time-color-change
 (fn [db [_ new-color]]
   (assoc db :time-color new-color)))

(defn color-input []
  [:div.color-input
   [:input.input {:type :text
                  :placeholder "Enter a color "
                  :on-change #(swap! db/app-db
                                     assoc :time-color
                                     (-> % .-target .-value))}]])

(rf/reg-sub
 :time-color
 (fn [db _]
   (:time-color db)))

(rf/reg-sub
 :time
 (fn [db _]
   (:time db)))

(defn clock []
  [:div
   {:style {:color @(rf/subscribe [:time-color])}}
   (-> @(rf/subscribe [:time])
       .toTimeString
       (clojure.string/split " ")
       first)])

(rf/reg-sub
 :time-color
 (fn [db _]
   (:time-color db)))

(defn ui []
  [:div.columns.is-centered>div.column.is-two-thirds
   [:p>h2 "The time is:"]
   [:div.columns>div.column
    [clock]
    [:br]
    [color-input]]])

(defn mount-app-element []
  (rdom/render
   [ui]
   (gdom/getElement "app")))


;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
