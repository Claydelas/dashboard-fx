create table if not exists CAMPAIGN (
    CID         INT auto_increment,
    NAME        VARCHAR,
    IMPRESSIONS INT,
    CLICKS      INT,
    UNIQUES     INT,
    BOUNCES     INT,
    CONVERSIONS INT,
    CTR         DOUBLE,
    CPA         DOUBLE,
    CPC         DOUBLE,
    CPM         DOUBLE,
    BOUNCE_RATE DOUBLE,
    TOTAL_COST  DOUBLE,
    PARSED      BOOLEAN default FALSE not null,
    constraint CAMPAIGN_PK
        primary key (CID));

create table if not exists IMPRESSION(
    DATE         DATETIME                                                               not null,
    USER         LONG                                                                   not null,
    GENDER       ENUM ('Male', 'Female')                                                not null,
    AGE          ENUM ('<25', '25-34', '35-44', '45-54', '>54')                         not null,
    INCOME       ENUM ('Low', 'Medium', 'High')                                         not null,
    CONTEXT      ENUM ('News', 'Shopping', 'Social Media', 'Blog', 'Hobbies', 'Travel') not null,
    COST         DOUBLE                                                                 not null,
    CID          INT                                                                    not null,
    IMPRESSIONID INT auto_increment,
    constraint IMPRESSION_PK
        primary key (IMPRESSIONID),
    constraint IMPRESSION_CAMPAIGN_CID_FK
        foreign key (CID) references CAMPAIGN (CID)
            on update cascade on delete cascade);

create table if not exists CLICK(
    DATE    DATETIME not null,
    USER    LONG     not null,
    COST    DOUBLE   not null,
    CID     INT      not null,
    CLICKID INT auto_increment,
    constraint CLICK_PK
        primary key (CLICKID),
    constraint CLICK_CAMPAIGN_CID_FK
        foreign key (CID) references CAMPAIGN (CID)
            on update cascade on delete cascade);

create table if not exists INTERACTION(
    ENTRY_DATE    DATETIME not null,
    USER          LONG     not null,
    EXIT_DATE     DATETIME,
    VIEWS         INT      not null,
    CONVERSION    BOOLEAN  not null,
    CID           INT      not null,
    INTERACTIONID INT auto_increment,
    constraint INTERACTION_PK
        primary key (INTERACTIONID),
    constraint INTERACTION_CAMPAIGN_CID_FK
        foreign key (CID) references CAMPAIGN (CID)
            on update cascade on delete cascade);
