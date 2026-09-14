document.addEventListener('DOMContentLoaded',()=>{
  const toggle=document.querySelector('.nav-toggle'),nav=document.querySelector('.nav-links');
  if(toggle&&nav)toggle.addEventListener('click',()=>nav.classList.toggle('open'));
});
