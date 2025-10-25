<?php
// DEBUG FUNCTIONS

// Multidimensional Array Debugs
function dbgArr( $arr, $title = '' ){

    if ( !is_array( $arr ) || ( sizeof( $arr ) == 0 ) ) {
        echo( 'empty array!' );
        return;
    }

    echo '<table border="1" nowrap>';

    if ( $title )
        echo '<th colspan="2"><font size="3"><b>' . $title . '</b></font></th>';

    echo '<tbody>';

    foreach( $arr AS $k => $v ) {
        echo "<tr><td><b>$k</b></td><td>";

        if ( is_array( $v ) )
            dbgArr( $v );
        else
            echo strip_tags( $v, '<B>' );

        echo '</td></tr>';
    }

    echo '</tbody></table>';
}

function d( $var ){
    echo '<PRE>';
    var_dump( $var );
    echo '</PRE><BR>';
}

function e_RuntimeReport(){
    error_reporting(E_ALL);
    ini_set('display_errors', 1);
}
