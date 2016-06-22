public class bubble {
    public static void bubble_default(int[] array){
        int tmp;
        for (int i=1;i<array.length;i++) {
            for (int j=0;j<array.length-i;j++) {
                if (array[j] > array[j+1]) {
                    tmp = array[j];
                    array[j] = array[j+1];
                    array[j+1] = tmp;
                }
            }
        }
    }
    
    public static void bubble_flag(int[] array) {
        int tmp;
        int exchange;
        for (int i=1;i<array.length;i++) {
            exchange = 0;
            for (int j=0;j<array.length-i;j++) {
                if (array[j] > array[j+1]) {
                    tmp = array[j];
                    array[j] = array[j+1];
                    array[j+1] = tmp;
                    exchange = 1;
                }
            }
            if (exchange == 0) {
                break;
            }
        }
    }


    public static void main(String[] args) {
        int array[] = {5,4,3,2,1};
        bubble_flag(array);
        for (int i:array) {
            System.out.println(i);
        }
    }

}
