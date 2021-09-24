/* 
 * Функции для работы админ-панели (фрагмента)
 */

// аналог событий FormLoad / Loaded / OnCreate - страница собрана и готова
document.addEventListener("DOMContentLoaded", function(){
    // ищем кнопку удаления
    const delBtn = document.getElementById("del-user-button");
    // проверяем успешность поиска
    if( ! delBtn ) throw "Admin script: del-user-button not found" ;
    // назначаем обработчик события
    delBtn.addEventListener("click", delBtnClick);
    
    // ищем <select name="userId">
    const sel = document.querySelector("select[name=userId]") ;
    // проверяем успешность поиска
    if( ! sel ) throw "Admin script: select[name=userId] not found" ;
    // назначаем обработчик события
    sel.addEventListener("change", selectUserChange);
});

function delBtnClick() {
    // получаем выбранного пользователя
    const item = document.querySelector("select[name=userId]");
    // проверяем выбор
    if( ! item ) throw "Admin script: select[name=userId] not found" ;
    
    const del = item[item.selectedIndex].getAttribute("del");
    console.log(del);
    if( del == "1" ) {  // Восстановление
        fetch( "admin/user?uid=" + item.value, { method: "put" } )
            .then(r=>r.text())
            .then(t=>{
                if(t == "Restore OK"){
                    location = location;
                } else {
                    alert(t);
                }
            });
    } else { // Удаление
        fetch( "admin/user?uid=" + item.value, { method: "delete" } )
            .then(r=>r.text())
            .then(console.log);
    }
}

// обработчик события изменения выбора пользователя
function selectUserChange(e) {
    const opt = e.target[e.target.selectedIndex];
    const del = opt.getAttribute("del") ;
    const delBtn = document.getElementById("del-user-button");
    if(del=="1"){
        delBtn.value = "Восстановить";
    } else {
        delBtn.value = "Удалить";
    }    
    //console.log(del);
}