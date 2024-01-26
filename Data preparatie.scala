import java.io.{BufferedWriter, File, FileWriter}
import scala.io.Source

object Convert extends App {

  val verp19 = Source.fromFile("./resources/verpl19.csv").getLines().toList

  val rit19 = Source.fromFile("./resources/rit19.csv").getLines().toList

  val pc4_2019_vol = Source.fromFile("./resources/pc4_2019_vol.csv").getLines().toList

  val pers19 = Source.fromFile("./resources/pers19.csv").getLines().toList

  val outputFileRemovedDensities = new File("./out/ritten2019-missendeVerwijderd.csv")

  val outputFileReplacedDensities = new File("./out/ritten2019-missendeVervangen.csv")


  //persnum, verpnum, nrit, hverv, vertpost, aankpost, duur_ned, afst_ned
  val trips = verp19
    .map(x => x.split(","))
    .filter(x => x(26).equals("0"))                                       //take only domestic trips
    .map(x => List(x(0), x(1), x(3), x(39), x(17), x(22), x(33), x(46)))  //take only relevant columns
    .map(x => x.map(y => y.toInt))                                        //cast all values to integers


  //(persnum, verpnum) -> persnum, verpnum, ritnum, verv, vertpost, aankpost, duur_ned, afst_ned
  val rides = rit19
    .drop(1)
    .map(x => x.split(","))
    .map(x => List(x(0), x(1), x(2), x(20), x(8), x(11), x(18), x(25))) //take only relevant columns
    .map(x => x.map(y => y.toInt))                                      //cast all values to integers
    .groupBy(x => (x(0), x(1)))                                         //group by persnum and verpnum


  //postcode -> addr_dichtheid
  val postcodes = pc4_2019_vol
    .drop(2)
    .map(x => x.split(","))
    .map(x => (x(0).toInt, x(132).toInt))    //take only relevant columns
    .toMap


  //persnum -> woongem, leeftijd, geslacht, ethnicit, inkh_s10
  val personData = pers19
    .drop(1)
    .map(x => x.split(","))
    .map(x => (x(0).toInt, List( x(13), x(20), x(21), x(22), x(54)).map(y => y.toInt))) //take only relevant columns
    .toMap


  //persnum, verpnum, ritnum, verv, vertpost, aankpost, duur_ned, afst_ned, woongem, leeftijd, geslacht, ethnicit, inkh_s10
  val totalRides = ((trips
        .flatMap(x => rides.get((x(0), x(1))))          //replace all trips with multiple rides with the rides themselves
        .flatten
      ) ::: trips.filter(x => x(2) == 1)                //add back the trips of only one ride that had been removed in previous step
    ).map(x => x ::: ((personData.get(x(0)) match {     //try to add personData for each ride
      case Some(v) => v                                 //add the personData if it is present
      case None => Nil                                  //add nothing if the personData was missing
    }) :: Nil).flatten)                                 //append the personData as separate columns


  //All rides with densities added, where rides with missing densities are removed
  //persnum, verpnum, ritnum, verv, vertpost, aankpost, duur_ned, afst_ned, woongem, leeftijd, geslacht, ethnicit, inkh_s10, vert_dichth, aank_dichth
  val totalRidesRemoveMissingDensities = totalRides
    .map(x => x ::: ((postcodes.get(x(4)) match { //try to append origin density (vert_dichth)
      case Some(v) => v                           //add the density if it was found for the postcode
      case None => -1                             //add temporary placeholder if the density was unknown or postcode was missing
    }) :: (postcodes.get(x(5)) match {            //try to append destination density (aank_dichth)
      case Some(v) => v                           //add the density if it was found for the postcode
      case None => -1                             //add temporary placeholder if the density was unknown or postcode was missing
    }) :: Nil))
    .filter(x => x(13) > 0 && x(14) > 0)          //remove trips with missing densities


  //All rides with densities added, where missing densities are replaced with an estimation.
  //persnum, verpnum, ritnum, verv, vertpost, aankpost, duur_ned, afst_ned, woongem, leeftijd, geslacht, ethnicit, inkh_s10, vert_dichth, aank_dichth
  val totalRidesReplaceMissingDensities = totalRides
    .map(x => x ::: (((x(4), postcodes.get(x(4))) match { //try to append origin density (vert_dichth)
      case (p, Some(v)) => v                              //add the density if it was found for the postcode
      case (0, None) => -1                                //add temporary placeholder if postcode was not present for ride
      case (p, None) => 400                               //add estimated density if density was unknown for the postcode
    }) :: ((x(5), postcodes.get(x(5))) match {            //try to append destination density (aank_dichth)
      case (p, Some(v)) => v                              //add the density if it was found for the postcode
      case (0, None) => -1                                //add temporary placeholder if postcode was not present for ride
      case (p, None) => 400                               //add estimated density if density was unknown for the postcode
    }) :: Nil))
    .filter(x => x(13)>0 && x(14)>0)                      //remove trips with missing densities





  //convert list to csv string
  val totalRidesRemoveMissingDensitiesStrings = totalRidesRemoveMissingDensities
    .map(x => x.foldLeft("")((a,b) => a ++ b.toString ++ ",").dropRight(1))

  //write to file
  outputFileRemovedDensities.createNewFile()
  val writer = new BufferedWriter(new FileWriter(outputFileRemovedDensities))
  writer.write("persnum,verpnum,ritnum,verv,vertpost,aankpost,duur_ned,afst_ned,woongem,leeftijd,geslacht,ethnicit,inkh_s10,vert_dichth,aank_dichth\n")
  totalRidesRemoveMissingDensitiesStrings.foreach(x => writer.write(x ++ "\n"))
  writer.close()


  //convert list to csv string
  val totalRidesReplaceMissingDensitiesStrings = totalRidesReplaceMissingDensities
    .map(x => x.foldLeft("")((a, b) => a ++ b.toString ++ ",").dropRight(1))

  //write to file
  outputFileReplacedDensities.createNewFile()
  val writer2 = new BufferedWriter(new FileWriter(outputFileReplacedDensities))
  writer2.write("persnum,verpnum,ritnum,verv,vertpost,aankpost,duur_ned,afst_ned,woongem,leeftijd,geslacht,ethnicit,inkh_s10,vert_dichth,aank_dichth\n")
  totalRidesReplaceMissingDensitiesStrings.foreach(x => writer2.write(x ++ "\n"))
  writer2.close()
}