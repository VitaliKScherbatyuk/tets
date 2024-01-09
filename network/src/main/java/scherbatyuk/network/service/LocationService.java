package scherbatyuk.network.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scherbatyuk.network.dao.GeoLocationResponseRepository;
import scherbatyuk.network.domain.GeoLocationResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

@Service
public class LocationService {

    @Autowired
    private GeoLocationResponseRepository geoLocationResponseRepository;

    public String updateVPN(HttpServletRequest request) {
        try {
            // Отримати реальний IP-адресу користувача
            String ipAddress = getRealIPAddress(request);

            // Створити або оновити запис з отриманою IP-адресою
            GeoLocationResponse geoLocationResponse = geoLocationResponseRepository.findById(1).orElse(new GeoLocationResponse());
            geoLocationResponse.setIpAddress(ipAddress);
            geoLocationResponse.setDataEnter(LocalDate.now());
            geoLocationResponseRepository.save(geoLocationResponse);

            return "VPN updated successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to update VPN";
        }
    }

    private String getRealIPAddress(HttpServletRequest request) {
        // Отримати IP-адресу з хедера "X-Forwarded-For"
        String ipAddress = request.getHeader("X-Forwarded-For");

        // Якщо хедер "X-Forwarded-For" не заданий, отримати IP-адресу зі змінної "RemoteAddr"
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }
}

