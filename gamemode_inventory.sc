//!scarpet v1.5

// stay loaded
__config() -> (
   m(
      l('stay_loaded','true')
   )
);


__command()->(
  player = player();
  if(player~'gamemode' == 'survival' && player~'permission_level' > 1
  , 
    print(player~'gamemode'+' gamemode');
    __store_player_inventory(player);
    modify(player, 'gamemode', 1);
    if(__stored_player_inventory_exists(player)
      ,
        schedule(0, '__restore_player_inventory', player);
      );
    return();
  ); 
  
  if(player~'gamemode' == 'creative' && player~'permission_level' > 1
    , 
    print(player~'gamemode'+' gamemode');
    __store_player_inventory(player);
    modify(player, 'gamemode', 0);
    if(__stored_player_inventory_exists(player)
      ,
        schedule(0, '__restore_player_inventory', player);
      );
 //   schedule(2,'__delete_stored_player_inventory', player);
    return();
  );
); 


__stored_player_inventory_exists(player) -> (
  if(read_file(player+'_'+player~'gamemode'+'_inv', 'json')
  , true 
  , false
  );
);

__store_player_inventory(player) -> (
  i = []; 
  loop(inventory_size(player)
  , i += encode_json(inventory_get(player,_));
  );
  write_file(player+'_'+player~'gamemode'+'_inv', 'json', i);
);


__stop_items_from_dropping(player) -> (
  loop(inventory_size(player), inventory_set(player,_,0));
);


__restore_player_inventory(player) -> (
  i = read_file(player+'_'+player~'gamemode'+'_inv', 'json');
  for(decode_json(i), inventory_set(player,_i,_:1,_:0,_:2));
);


__delete_stored_player_inventory(player) -> (
  delete_file(player+'_'+player~'gamemode'+'_inv', 'json');
);

