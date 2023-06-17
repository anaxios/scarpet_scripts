__config() -> {
    'command_permission' -> 'ops',
    'commands' -> {
        '' -> _() -> print('this is a root call, does nothing. Just for show'),
        'gen' -> _() -> run_reset(pos(player()), 1, -1),
        'gen <pos> <radius> <inhabited_time>' -> 'run_reset',
    },
    'arguments' -> {
        'x' -> {'type' -> 'pos'},
        'radius' -> {'type' -> 'int', 'suggest' -> [1]},
        'inhabited_time' -> {'type' -> 'int', 'suggest' -> [300]},
    }
};

run_reset(pos, r, t) -> (
    if(player()~'gamemode' == 'creative',
        pos = map(pos, _ - (_ % 16));
        chunk = 16;
        r = chunk * r;

        task_thread('regen', _(outer(pos),outer(r),outer(t))->(
            for(range(pos:0 - r, pos:0 + r + 1 + chunk, 16), 
                x = _;
                for(range(pos:2 - r, pos:2 + r + 1 + chunk, 16), 
                    inhabited = inhabited_time(x, 0, y)/20;
                    y = _;

                    if(inhabited <= t || -1 == t, 

                        reset_chunk(x,0,y);
                        print('regen: '+x+', 0, '+y+' time: '+inhabited);
                        ,print('skip: '+x+', 0, '+y+' time: '+inhabited)
                    );
                    //set(x, top('surface', x, 0, y), y, 'red_concrete');
                );
            );
        ));

    , print(format('r ERROR: must be in creative mode'))
    );
);