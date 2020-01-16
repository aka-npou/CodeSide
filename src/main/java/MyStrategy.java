import model.*;
import strategy.AkaNpouStrategy;
import strategy.*;


//cd "E:\java\mail cups\ai\big\2019\aicup2019-windows"
// .\aicup2019.exe --config "E:\java\mail cups\ai\big\2019\aicup2019-windows\config no spread.json"

public class MyStrategy {

    AkaNpouStrategy akaNpouStrategy = new Strategy_v0();

    public UnitAction getAction(Unit unit, Game game, Debug debug) {

        return akaNpouStrategy.getAction(unit, game, debug);
    }

}