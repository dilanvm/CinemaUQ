module edu.uniquindio.cinemauq {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.pdfbox;

    exports edu.uniquindio.cinemauq.domain;
    exports edu.uniquindio.cinemauq.domain.estado;
    exports edu.uniquindio.cinemauq.application;
    exports edu.uniquindio.cinemauq.infrastructure;
    exports edu.uniquindio.cinemauq.app;
    exports edu.uniquindio.cinemauq.presentation;

    opens edu.uniquindio.cinemauq.domain to javafx.fxml;
    opens edu.uniquindio.cinemauq.domain.estado to javafx.fxml;
    opens edu.uniquindio.cinemauq.application to javafx.fxml;
    opens edu.uniquindio.cinemauq.infrastructure to javafx.fxml;
    opens edu.uniquindio.cinemauq.app to javafx.fxml;
    opens edu.uniquindio.cinemauq.presentation to javafx.fxml;
}
