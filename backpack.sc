//!scarpet v1.5

// stay loaded
__config() -> (
   m(
      l('stay_loaded','true')
   )
);

__on_player_uses_item(player, item_tuple, hand) -> (
    if(item_tuple:0 == 'bundle'
    ,   
        inventory_set(player, query(player, 'selected_slot'), 1, 'bundle');
        screen = create_screen(player(),'generic_9x6', 'bundle', _(screen, player, action, data) -> (
            if(action == 'throw' && data:'slot' == player~'selected_slot'+81
            ,
                inventory_set(screen, data:'slot', 0);
                'cancel';
            );
            if(action == 'slot_update' && data:'slot' == player~'selected_slot'+81
            ,
                //print(data);
                inventory_set(screen, data:'slot', 1, 'bundle');
                inventory_set(screen, -1, 0);
                'cancel';
            );
            
            if(action == 'close'
            ,   
                tag = [];
                for(range(54)
                ,
                    i = inventory_get(screen, _);
                    if(i, 
                        if(i:2
                        ,tag += nbt('{Count: '+i:1+'b, id: "minecraft:'+i:0+'",' + 'tag:' + i:2 + '}');
                        ,tag += nbt('{Count: '+i:1+'b, id: "minecraft:'+i:0+'",' + 'tag:' + '{}' + '}');
                        //,tag += nbt('{Count: '+i:1+'b, id: "minecraft:'+i:0+'",' + '}');
                        );
                    );
                );
                items = '{Items:[' + join(',', tag) + ']}';
                //print(items);
                if(tag
                ,    
                inventory_set(player, query(player, 'selected_slot'), 1, 'bundle', items);
                );
                sound('item.bundle.remove_one', pos(player));
            //print('closed');
            );
        ));
        
        task(_(outer(screen),outer(item_tuple),outer(player))->(
            if(screen_property(screen, 'open') == 'true'
            ,   
                //print(item_tuple:2:'Items');
                i = parse_nbt(item_tuple:2:'Items');
             //   print(i);
                if( i != 'null'
                , 
                    for(i
                    , 
              //        print(encode_nbt(_:'tag'));
                      if(encode_nbt(_:'tag')
                      , inventory_set(screen, _i, _:'Count', _:'id', encode_nbt(_:'tag'));
                      , inventory_set(screen, _i, _:'Count', _:'id');
                      );
                    );
                );
            );
        ));
    );
);