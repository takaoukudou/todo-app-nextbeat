window.addEventListener('load', function() {
  const colorMap = {
    1: 'category-red',
    2: 'category-blue',
    3: 'category-green',
    4: 'category-yellow',
    5: 'category-pink',
  };
  const categories = document.getElementsByClassName('category');
  for (const category of categories) {
    category.classList.add(
        colorMap[category.getElementsByClassName('category_color')[0].value]);
  }
});