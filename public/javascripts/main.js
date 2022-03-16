window.addEventListener('load',function(){
    const colorMap = {
        1:"category-front",
        2:"category-back",
        3:"category-infra"
    }
    const categories = document.getElementsByClassName("category")
    for(const category of categories){
        category. classList.add(colorMap[category.getElementsByClassName("category_color")[0].value])
    }
})