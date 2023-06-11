//!scarpet v1.5

// stay loaded
__config() -> (
   m(
      l('stay_loaded','true')
   )
);


__on_start() -> (
    global_average_load_time = 0;
    times = 100;
    
    for(range(times)
    , t = time();
    reset_chunk(pos(player()));
    global_average_load_time += time() - t;
    );
    print(__in_seconds(global_average_load_time/times));
);

__in_seconds(milliseconds) -> (
    milliseconds/1000
);


//rect(centre, range?, upper_range?)

