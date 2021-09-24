document.addEventListener("DOMContentLoaded", function(){
    // ищем все элементы, имеющие класс "task-del"
    for( let t of document.querySelectorAll(".task-del") ) {
        t.addEventListener("click", delTaskClick);
    }
});
function delTaskClick(e) {
    if( e.target.style.height == "auto" ) {
        e.target.style.height = "2vw" ;
    } else {
        e.target.style.height = "auto" ;
    }
}

function calendarClick(id) {
    // console.log(id);
    // event - объект-событие
    // event.target - img-календарь
    // event.target.parentNode - блок-задача
    if(event.target.inputCreated === true) {
        event.target.parentNode.removeChild(event.target.nextSibling);
        event.target.inputCreated = false;
    } else {
        const d = document.createElement("input");
        d.type = "date";
        d.taskId = id;
        d.addEventListener("change",deadlineChanged);
        event.target.parentNode
                .insertBefore(d, event.target.nextSibling);
        event.target.inputCreated = true;
    }
}

function deadlineChanged(e) {
    // e.target - input с новой датой
    // console.log(e.target.value);
    // e.target.previousSibling.inputCreated = false;
    // после ввода - удаляем input
    // e.target.parentNode.removeChild( e.target );
    // перезагрузка страницы обновит ее состав - код выше не нужен
    location = location.pathname + "taskcontrol/" + e.target.taskId 
            + "?action=date&date=" + e.target.value;
}

function descriptionEditClick(id) {
    // находим узел (Р) с текстом описания
    const p = event.target.parentNode.querySelector("p");
    if(!p) throw "P not found";
    
    if( p.getAttribute("contenteditable") == 'true' ) {  // завершили редактирование        
        // извлекаем текст описания
        const t = p.innerText;
        // задание: если текст не изменялся, то не отправлять изменения
        // сравниваем с сохраненным значением
        if(p.prevText == t) {  // нет изменений - только меняем кнопку
            // убираем атрибут редактирования
            p.removeAttribute("contenteditable");
            // меняем рисунок на кнопке
            event.target.style.backgroundPositionY = "0";
        } else {  // есть изменения
            // отправляем их на сервер для внесения изменений в БД
            location = location.pathname + "taskcontrol/" + id 
                + "?action=description&value=" + t;
        }        
    } else {  // начали редактирование        
        // запоминаем исходный текст (для проверки были ли изменения)
        p.prevText = p.innerText;
        // устанавливаем атрибут редактирования
        p.setAttribute("contenteditable","true");
        // фокусируем - ставим на курсор на элемент
        p.focus();
        // меняем рисунок на кнопке
        event.target.style.backgroundPositionY = "47%";
        
    }    
}