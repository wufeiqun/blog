"use strict";
function bubble_default(array) {
    var length = array.length;
    var tmp;
    if (length <= 1) {
        return array;
    }
    for (let i=1;i<length;i++) {
        for (let j=0;j<length-i;j++) {
            if (array[j] > array[j+1]) {
                tmp = array[j];
                array[j] = array[j+1];
                array[j+1] = tmp;
            }
        }
    }
    return array;
}

function bubble_flag(array) {
    var length = array.length;
    var tmp;
    var exchange;
    if (length <= 1) {
        return array;
    }
    for (let i=1;i<length;i++){
        exchange = false;
        for (let j=0;j<length-i;j++){
            if (array[j] > array[j+1]){
                tmp = array[j];
                array[j] = array[j+1];
                array[j+1] = tmp;
            }
        }
        if (!exchange){
            break 
        }
    }
    return array;
}

var array = [5,4,3,2,1]

console.log(bubble_default(array))
console.log(bubble_flag(array))
