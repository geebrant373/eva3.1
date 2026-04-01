/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.GameSystem;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Stream;

public class CleanListFile {
    public static void main(String[] args) {
    	Path input = Paths.get("C:\\Users\\kim\\Desktop\\list파일수정\\list.txt");
        Path output = Paths.get("C:\\Users\\kim\\Desktop\\list파일수정\\output.txt");

        if (!Files.exists(input)) {
            System.err.println("입력 파일이 없습니다: " + input.toAbsolutePath());
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            try (Stream<String> lines = Files.lines(input, StandardCharsets.UTF_8)) {
                lines.forEach(line -> {
                    String cleaned = cleanLine(line);
                    try {
                        writer.write(cleaned);
                        writer.newLine();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
            System.out.println("처리 완료. 결과 파일: " + output.toAbsolutePath());
        } catch (UncheckedIOException uio) {
            System.err.println("파일 쓰기 중 오류: " + uio.getCause().getMessage());
        } catch (IOException e) {
            System.err.println("I/O 오류: " + e.getMessage());
        }
    }

    /**
     * 주어진 줄을 다음 규칙으로 정리해서 반환한다:
     *  - 영어 알파벳(A-Z, a-z) 모두 삭제
     *  - 문자 '.', '(', ':', ')' 삭제
     *  - 숫자와 '<', ']', '!' 만 허용하고 그 밖의 연속된 문자들은 한 칸으로 치환
     *  - 양 끝 공백 제거 및 여러 공백은 단일 공백으로 축소
     *
     *  (사용자 요구: '<', ']' 및 '!'는 삭제하지 않음)
     */
    private static String cleanLine(String s) {
        if (s == null || s.isEmpty()) return "";

        // 1) 영어 제거
        String t = s.replaceAll("[A-Za-z]+", "");

        // 2) 삭제할 기호들 삭제: . ( : )
        t = t.replaceAll("[\\.():]", "");

        // 3) 숫자, '<', ']', '!' 외의 모든 연속 문자를 공백 하나로 치환
        //    (콤마, 대괄호 '[', 탭, 기타 문자 등은 이 단계에서 공백으로 바뀐다)
        t = t.replaceAll("[^0-9<>\\]!]+", " ");

        // 4) 앞뒤 공백 제거 및 여러 공백을 한 칸으로 줄임
        t = t.trim().replaceAll("\\s+", " ");

        return t;
    }
}
