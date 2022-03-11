 function setColorClass(targetId) {
    const colorMap = {
        1:"category-front",
        2:"category-back",
        3:"category-infra"
    }
    const category = document.getElementById(targetId)
    category.classList.add(colorMap[category.getElementsByClassName("category_color")[0].value])
}

setColorClass("category1")
setColorClass("category2")
setColorClass("category3")