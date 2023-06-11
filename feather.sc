//!scarpet v1.5

// stay loaded
__config() -> (
   m(
      l('stay_loaded','true')
   )
);

__on_player_dies(player) -> (
  if(__holding_feather(player)
  , 
    inventory_remove(player, 'feather', 1);
    __store_player_inventory(player);
    __stop_items_from_dropping(player); 
  ); 
);


__on_player_respawns(player) -> (
  if(__stored_player_inventory_exists(player)
  , schedule(0, '__restore_player_inventory', player);
    schedule(2,'__delete_stored_player_inventory', player);
  );
);

__holding_feather(player) -> (
  offhand = inventory_get(player, -1);
  if(offhand:0 == 'feather'
  , true;
  , false;
  );
);

__stored_player_inventory_exists(player) -> (
  if(read_file(player+'_feather_inv', 'json')
  , true 
  , false
  );
);

__store_player_inventory(player) -> (
  i = []; 
  loop(inventory_size(player)
  , i += encode_json(inventory_get(player,_));
  );
  write_file(player+'_feather_inv', 'json', i);
);


__stop_items_from_dropping(player) -> (
  loop(inventory_size(player), inventory_set(player,_,0));
);


__restore_player_inventory(player) -> (
  i = read_file(player+'_feather_inv', 'json');
  for(decode_json(i), inventory_set(player,_i,_:1,_:0,_:2));
);


__delete_stored_player_inventory(player) -> (
  delete_file(player+'_feather_inv', 'json');
);

