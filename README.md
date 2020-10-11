# Simple RecyclerView
simple recyclerview setup for android with kotlin dsl

* simple vertical/horizontal list/grid 
* with header and footer
* item with swipe menu
* no need for create multiple classes for adapter or viewholder
* simply use different snaps

## Importing the library
add the following lines to your app module build.gradle

```gradle
dependencies {
    implementation 'com.codegames:simplelist:0.300'
}
```

## Examples
For see a demo you can clone this project, build and run it

### Example 1 - vertical list

<img 
    src="https://raw.githubusercontent.com/shahab-yousefi/simple-list/master/screenshots/vertical-list.gif" 
    alt="android vertical list" 
    width="auto" 
    height="320" />

```kotlin
recyclerView.simple(items) { // call this function on your recycle view and put your dataset
    columns = 1 // a list with one column and many rows
    
    itemMargin(8) // margin for every items (dp)
    padding(8) // padding of recyclerView (dp)
    clipToPadding = false
    
    // set a header on top of list view that will scroll with other items
    headerBind(R.layout.header_view) {
        // bind header view with dataset
        // will run iside onBindViewHolder of header
    }

    // set a item layout and bind your dataset with it
    itemHolder(R.layout.item_view_v) {
        // will run inside item viewholder
        itemView.button.setOnClickListener {
            // on click
        }

        bind { view, item, position ->
            // bind item view with dataset
            // will run inside onBindViewHolder of item
        }
    }

    // set a footer at bottom of list view that will scroll with other items
    footerBind(R.layout.footer_view) { v ->
        // bind footer view with dataset
        // will run iside onBindViewHolder of footer
    }
}
```

### Example 2 - horizontal list

<img 
    src="https://raw.githubusercontent.com/shahab-yousefi/simple-list/master/screenshots/horizontal-list.png" 
    alt="android horizontal list" 
    width="auto" 
    height="320" />
  
```kotlin
recyclerView.simple(items) {
    rows = 1 // a list with one row and many columns
    
    itemMargin(8)
    padding(8)
    clipToPadding = false
    
    itemHolder(R.layout.item_view_g) {
        itemView.updateLayoutParams {
            width = 170 * density
        }

        itemView.setOnClickListener {
            // on click
        }

        bind { v, item, _ ->
            // bind item view
        }
    }
}
```

### Example 3 - vertical/horizontal grid

<div style="display:flex;">
    <img 
        src="https://raw.githubusercontent.com/shahab-yousefi/simple-list/master/screenshots/vertical-grid.gif" 
        alt="android vertical grid" 
        width="auto" 
        height="320" />
    <img 
        src="https://raw.githubusercontent.com/shahab-yousefi/simple-list/master/screenshots/horizontal-grid.png" 
        alt="android horizontal grid" 
        width="auto" 
        height="320" />
</div>
  
```kotlin
recyclerView.simple(items) {
    columns = 2 // a grid with 2 columns and many rows
    // or
    rows = 2 // a grid with 2 rows and many columns
    
    itemMargin(8)
    padding(8)
    clipToPadding = false
    
    headerBind(R.layout.header_view) {
        // bind header view
    }

    itemHolder(R.layout.item_view_g) {
        bind { v, item, _ ->
            // bind item view
        }
    }

}
```

### Example 4 - view pager

<img 
    src="https://raw.githubusercontent.com/shahab-yousefi/simple-list/master/screenshots/view-pager.gif" 
    alt="android recycler view view pager" 
    width="auto" 
    height="320" />
    
```kotlin {
recyclerView.simple(items) {
    rows = 1
    enablePagerSnap = true // list will act like a view pager
        
    itemBind(R.layout.item_view_p) { v, item, _ ->
        // bind item view
    }
}
```
### Example 5 - swipe menu

<img 
    src="https://raw.githubusercontent.com/shahab-yousefi/simple-list/master/screenshots/swipe-menu.gif" 
    alt="android recycler view view pager" 
    width="auto" 
    height="320" />
    
```kotlin
recyclerView.simple(items) {
    columns = 1
    
    itemMargin(8)
    padding(8)
    clipToPadding = false
    clipChildren = false

    itemHolder(R.layout.item_view_h, R.layout.menu_view_h) { // first input is item layout and second one is swipe menu layout

        bind { v, item, _ ->
            // bind item view
        }

        swipeView?.also { v ->
            v.btnDelete.setOnClickListener {
                adapter.removeItem(adapterPosition)
            }
            v.btnMessage.setOnClickListener {
                // on click
            }
        }

    }
}
```

## Methods/Variables

**`RecyclerView.sample(items) {}`**

start point of all functionality of this library. items can be List, MutableList, Array and ArrayList.
<br />
all other methods or variables must be inside this function

---

**`rows`**

set number of rows of list. one of rows or columns will be used.

---

**`columns`**

set number of columns of list. one of rows or columns will be used.

---

**`headerBind(layoutId) { view, item, position -> }`**

**`itemBind(layoutId) { view, item, position -> }`**

**`footerBind(layoutId) { view, item, position -> }`**

headerBind is for binding header view. 

itemBind is for binding item view.

footerBind is for binding footer view.

inputs: view is layoutId created view. item is from items and position is position of view inside recycleView.

position is not always equal to item position. if list has header, position will be one more than item position

---

**`adapter`**

is adapter of the recyclerview


---

**`adapter.getItemPosition(position)`**

convert adapter position to item position

---

**`adapter.getItem(position)`**

receive adapterPosition and return item from dataset that is in itemPosition

---

**`adapter.removeItem(position)`**

receive adapterPosition and delete item from dataset that is in itemPosition and update adapter

---

**`adapter.removeItemRange(positionStart, itemCount)`**

remove range of items and update adapter

---

**`adapter.addItemRange(positionStart, items)`**

add range of items and update adapter

---

**`adapter.addItemRange(items)`**

add range of items to end of the list and update adapter

---

**`adapter.addItem(position, item)`**

add item to position of the list and update adapter

---

**`adapter.addItem(item)`**

add item to end of the list and update adapter

---

**`adapter.setItem(item)`**

set item to position of the list and update adapter

---

**`adapter.swipeItem(position1, position2)`**

replace items in two positions and update adapter

---

**`headerHolder(layoutId) {}`**

headerHolder is header viewHolder. 

you can bind inside header holder with `bind() { view, item, position -> }`.

this is equivalent of headerBind. 

---

**`itemHolder(itemLayoutId, swipeLayoutId) {}`**

itemHolder is item viewHolder.

you can bind inside item holder with `bind() { view, item, position -> }` this is equivalent of itemBind.

you can bind swipe menu with `swipeBind() { view, item, position -> }`. 

**`item`** : item from dataset

**`rootView`** : root view of item that contains itemView and swipeView. if swipeLayoutId be null then itemView will be equal to rootView

**`itemView`** : created view of itemLayourId

**`swipeView`** : created view of swipeLayoutId

---

**`footerHolder(layoutId) {}`**

footerHolder is footer viewHolder. 

you cand bind inside footer holder with `bind() { view, item, position -> }`. this is equivalent of footerBind.

---

**`clipToPadding`** : equivalent of recyclerView.clipToPadding

**`clipChildren`** : equivalent of recyclerView.clipChildren

**`itemMargin(space)`** : top and bottom and left and right margin of every item (dp)

**`itemHorizontalMargin(space)`** : left and right margin of every item (dp)

**`itemVerticalMargin(space)`** : top and bottom margin of every item (dp)
 
---

**`enablePagerSnap = true`** : list will be act like viewPager 

**`enableLinearSnap = true`** : linear snap

**`enableGravitySnap = Gravity.CENTER`** : set gravity for items to snap ([GravitySnapHelper](https://github.com/rubensousa/GravitySnapHelper))

---

**`swipeMode`** : SWIPE_MODE_NORMAL | SWIPE_MODE_SAME_LEVEL ([SwipeRevealLayout](https://github.com/chthai64/SwipeRevealLayout))

**`swipeDragEdge`** : SWIPE_DRAG_EDGE_BOTTOM | SWIPE_DRAG_EDGE_LEFT | SWIPE_DRAG_EDGE_RIGHT | SWIPE_DRAG_EDGE_BOTTOM ([SwipeRevealLayout](https://github.com/chthai64/SwipeRevealLayout))


## Used Libraries

* [GravitySnapHelper](https://github.com/rubensousa/GravitySnapHelper) | A SnapHelper that snaps a RecyclerView to an edge.

* [SwipeRevealLayout](https://github.com/chthai64/SwipeRevealLayout) | A layout that you can swipe/slide to show another layout.
