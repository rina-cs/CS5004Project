import { createVuetify } from "vuetify";
import "vuetify/styles";

export default createVuetify({
  theme: {
    defaultTheme: "light",
    themes: {
      light: {
        colors: {
          primary: "#1976D2",
          secondary: "#424242",
          accent: "#FF4081",
          success: "#4CAF50",
          error: "#FF5252",
          warning: "#FFC107",
          background: "#FFFFFF",
        },
      },
      dark: {
        colors: {
          primary: "#1E88E5",
          secondary: "#212121",
          accent: "#FF4081",
          success: "#00E676",
          error: "#D32F2F",
          warning: "#FFEA00",
          background: "#121212",
        },
      },
    },
  },
});
