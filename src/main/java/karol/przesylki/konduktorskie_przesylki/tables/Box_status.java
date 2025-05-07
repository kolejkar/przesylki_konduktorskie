package karol.przesylki.konduktorskie_przesylki.tables;
public enum Box_status {

    PayedPost, //paczka opłacona
    AcceptPost, //paczka nadana w pociągu
    InProgressPost, //paczka w trakcie transportu do odbiorcy
    DeliveredPost, //paczka dostarczona
    OutLimitPost //przechowywanie paczki
}
