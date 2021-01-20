(ns demo
  (:require
   [goog.dom :as gdom]))

;;; Reference:

;; https://google.github.io/closure-library/api/

(set! *warn-on-infer* true)

;;------------------------------------------------------------------------------
;; Canvas

(defn context
  "Takes a canvas element and returns its 2D context."
  [canvas]
  (.getContext canvas "2d"))

(defn canvas
  "Create a new canvas element."
  [& options]
  (let [options' (apply hash-map options)
        canvas   (gdom/createDom "canvas" (clj->js options'))
        ctx      (context canvas)]
    ;; Not sure if these are needed. If they are, default options must be handled.
    ;;(set! (-> canvas .-style .-width)  (str w "px"))
    ;;(set! (-> canvas .-style .-height) (str h "px"))
    ;;(.scale ctx scale scale)
    (set! (.-imageSmoothingEnabled ctx) false)
    canvas))

(defn draw-rect
  "Draw a rectangle on canvas."
  [canvas x y w h color]
  (let [ctx (context canvas)]
    (set! (.-fillStyle ctx) color)
    (.fillRect ctx x y w h)))

;;------------------------------------------------------------------------------
;; Events

;; https://stackoverflow.com/questions/55677/how-do-i-get-the-coordinates-of-a-mouse-click-on-a-canvas-element/18053642#18053642
(defn get-cursor-position
  "Returns the position of the cursor given an element and an event. Returns a map
  with :x and :y keys."
  [element event]
  (let [rect (.getBoundingClientRect element)
        x    (- (.-clientX event) (.-left rect))
        y    (- (.-clientY event) (.-top rect))]
    {:x x :y y}))

;;------------------------------------------------------------------------------
;; Utilities

(defn draw-cell
  "Fill in cell i,j on canvas with color given grid-w and grid-h."
  [canvas grid-w grid-h i j color]
    (let [canvas-w (.-width canvas)
          canvas-h (.-height canvas)
          cell-w   (/ canvas-w grid-w)
          cell-h   (/ canvas-h grid-h)
          x        (* i cell-w)
          y        (* j cell-h)]
      (draw-rect canvas x y cell-w cell-h color)))

;;------------------------------------------------------------------------------
;; Main entry point

(defn ^:export start []

  (js/console.log "ClojureScript demo project started")

  ;; Defining everything to make it easier to work with interactively. This is
  ;; not "reloadable" code.

  ;;; Parameters

  (def canvas-w
    "The width of the canvas, in pixels."
    1000)

  (def canvas-h
    "The height of the canvas, in pixels."
    500)

  (def grid-w
    "The width of the grid, in columns."
    100)

  (def grid-h
    "The height of the grid, in rows."
    50)

  (def update-freq
    "How long to wait between updates, in milliseconds.

  Can be redefined while running!"
    100)

  (def max-iterations
    "The maximum number of updates.

  Can be redefined while running!"
    200)

  ;;; State

  (def app
    "The main application HTML element."
    (.getElementById js/document "app"))

  ;; Remove all children from the main app (e.g., to remove all canvases and
  ;; their events).
  #_(gdom/removeChildren app)

  (def canvas-1
    "The canvas element."
    (canvas :id "demo-canvas" :width canvas-w :height canvas-h))

  ;; Add the canvas to the app element.
  (gdom/append app canvas-1)

  ;; Initialize state. It's wrapped in an atom so that it can be updated via
  ;; events.
  (def state (atom (into {} (for [i (range 0 grid-w)
                                  j (range 0 grid-h)]
                              [[i j] false]))))

  ;;; User events.

  (def on-click-handler
    "A mouse click event handler."
    (fn [event]
      (let [target        (.-target event)
            {:keys [x y]} (get-cursor-position target event)
            cell-w        (/ canvas-w grid-w)
            cell-h        (/ canvas-h grid-h)
            i             (quot x cell-w)
            j             (quot y cell-h)]
        (js/console.log "click: "
                        (clj->js {:x x :y y})
                        " => "
                        (clj->js {:i i :j j}))
        (draw-cell canvas-1
                   grid-w grid-h
                   i j
                   "#aa0000"))))

  ;; Add the click handler to the canvas.
  (.addEventListener canvas-1 "click" on-click-handler false)

  ;; Remove the click handler from the canvas.
  ;; Note that you must do this BEFORE redefining on-click-handler!
  #_(.removeEventListener canvas-1 "click" on-click-handler false)

  ;;; Update loop.

  ;; TODO: Actually do something with state. Not sure how to determine what
  ;; changed efficiently.
  (defn update-state
    "Update state, whatever that means."
    [state]
    (let [i (rand-int grid-w)
          j (rand-int grid-h)]
      (draw-cell canvas-1
                 grid-w grid-h
                 i j
                 (str "rgb(" (rand-int 256) "," (rand-int 256) "," (rand-int 256) ")"))))

  ;; https://developer.mozilla.org/en-US/docs/Learn/JavaScript/Asynchronous/Timeouts_and_intervals
  ;; https://developer.mozilla.org/en-US/docs/Web/API/WindowOrWorkerGlobalScope/setTimeout

  (letfn [(step [i]
            (if (<= i max-iterations)
              (do (js/console.log "step: " i)
                  ;; The time it takes to perform the update is subtracted from
                  ;; the delay (update-freq), so that the updates don't drift.
                  (let [start-time (.now js/Date)]
                    (swap! state update-state)
                    (let [end-time (.now js/Date)
                          delta    (- end-time start-time)]
                      (js/setTimeout step (- update-freq delta) (inc i)))))
              (js/console.log "done")))]
    (js/setTimeout step update-freq 1))

  )
