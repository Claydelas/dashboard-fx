import group18.dashboard.model.Campaign;

public class Test {

    public Campaign campaign = new Campaign();

    public static void main(String[] args) throws Exception {
        //--------
        loadDataTimings();
        //--------

        /*File file = new File(Test.class.getClassLoader().getResource("2_week_campaign_2/impression_log.csv").getFile());

        CsvMapper<Impression> mapper =
                CsvMapperFactory
                        .newInstance()
                        .ignoreColumns("Age")
                        .addColumnDefinition("Date", CsvColumnDefinition.dateFormatDefinition("dd-MM-yyyy hh:mm:ss"))
                        .addAlias("Date", "date")
                        .addAlias("Gender", "gender")
                        .addAlias("Income", "income")
                        .addAlias("Context", "context")
                        .addAlias("Impression Cost", "cost")
                        .newMapper(Impression.class);

        CsvParser.mapWith(mapper).forEach(file, row -> row.getDate());

        CsvMapper<Impression> mapper = CsvMapper.builder(Impression.class)
                .stringField("route_id")
                .quoted()
                .optional()
                .stringField("service_id")
                .required()
                .build();

        CsvReader<Impression> csvReader = mapper.create(new ParallelReader(new BufferedReader(new FileReader(file))));
        //.headers("Date", "ID", "Gender", "Age", "Income", "Context", "Impression Cost")*/
    }

    static void loadDataTimings() throws Exception {
        Campaign.benchmarks("D:\\Projects\\Java\\2_week_campaign");
    }

}
